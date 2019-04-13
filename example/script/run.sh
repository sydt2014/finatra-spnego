#!/usr/bin/env bash

set -e

cd $(dirname "$0")

DEBUG=true
WWW_FOLDER=public

jar_file_location="$1"
shift

jar_dir=$(dirname "${jar_file_location}")
jar_name=$(basename "${jar_file_location}")

cd "${jar_dir}"

jar -xf "${jar_name}" jaas-krb5.conf
jar -xf "${jar_name}" krb5.conf

if [[ "${DEBUG}" == "true" ]]; then
  export KRB5_TRACE=/dev/stderr
fi

java \
  -Djava.security.auth.login.config=jaas-krb5.conf \
  -Djava.security.krb5.conf=krb5.conf \
  -Djavax.security.auth.useSubjectCredsOnly=false \
  -Dsun.security.spnego.debug="${DEBUG}" \
  -Dsun.security.krb5.debug="${DEBUG}" \
  -jar "${jar_name}" -doc.root="${WWW_FOLDER}" $@