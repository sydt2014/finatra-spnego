#!/usr/bin/env bash

set -e

cd $(dirname "$0")/..

kdc_container_name="krb5-kdc-server-example-com"
container_name="spnego-server"
service_name="${container_name}"
principal_server="HTTP/spnego-server.example.com@EXAMPLE.COM"

# Delete container if already exists
container_id=$(docker ps -a -q -f name="${container_name}")
if [[ -n "${container_id}" ]]; then
    echo "INFO: Remove ${container_name} container"
    docker rm --force "${container_id}"
fi

# Create container for spnego server
docker run -d -it \
  --network=example.com --name="${service_name}" --hostname="${service_name}.example.com" \
  minimal-ubuntu:latest bash


# Provisioning principal to KDC and extract keytab
docker exec "${kdc_container_name}" /bin/bash -c "
rm -vf /tmp/${service_name}.keytab # force to re-create keytab from scratch
cat << EOC  | kadmin.local
add_principal -randkey ${principal_server}
ktadd -k /tmp/${service_name}.keytab -norandkey ${principal_server}
quit
EOC
"

# Build project
make build

# Deploy project on container
docker cp "./script/run.sh" "${container_name}:/root/run.sh"
docker cp "./build/libs/spnego-server-1.0-SNAPSHOT-all.jar" "${container_name}:/root/spnego-server.jar"

# Deploy keytab
docker cp "${kdc_container_name}:/tmp/${service_name}.keytab" "/tmp/${service_name}.keytab"
docker cp "/tmp/${service_name}.keytab" "${container_name}:/tmp/${service_name}.keytab"

# Right permission for keytab
docker exec "${container_name}" /bin/bash -c "
apt-get update
apt-get -y install krb5-user
chmod 400 /tmp/spnego-server.keytab
klist -kte /tmp/spnego-server.keytab
ls -l /tmp/spnego-server.keytab
"

# Run project
docker exec -d "${container_name}" /bin/bash -c "
./run.sh spnego-server.jar > spnego-server.log
"

container_ip=$(docker exec "${container_name}" /bin/bash -c 'hostname -I | cut -f1 -d" "')

read -p "Do you want update /etc/hosts/ for this container ip '${container_ip}'? [Y/n]: " answer
if [[ "${answer}" == "Y" ]]; then
  entry=$(grep -E "^${container_ip}" /etc/hosts)
  if [[ -n "${entry}" ]]; then
    >&2 echo "ERROR: '${entry}' already exists for this container ip '${container_ip}'!"
    exit 1
  fi
  sudo echo "${container_ip}\t${container_name}.example.com ${container_name}" >> /etc/hosts
fi

# Connect to container
docker exec -it "${container_name}" bash
# To see server log do:
# tail -f spnego-server.log
# Crtl+D to go out docker container
