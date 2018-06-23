#!/usr/bin/env bash

kdc_container="krb5-kdc-server-example-com"
principal_server="HTTP/spnego-server@EXAMPLE.COM"
service_name="spnego-server"

docker exec "${kdc_container}" /bin/bash -c "
rm /tmp/${service_name}.keytab
cat << EOC  | kadmin.local
add_principal -randkey ${principal_server}
ktadd -k /tmp/${service_name}.keytab -norandkey ${principal_server}
quit
EOC
"

docker cp "${kdc_container}:/tmp/spnego-server.keytab" /tmp/spnego-server.keytab
chmod 400 /tmp/spnego-server.keytab
klist -kt /tmp/spnego-server.keytab
ls -l /tmp/spnego-server.keytab