#!/usr/bin/env bash

cd $(dirname "$0")

debug=true

jar_file_location="$1"
shift

jar_dir=$(dirname "${jar_file_location}")
jar_name=$(basename "${jar_file_location}")

cd "${jar_dir}"

unzip "${jar_name}" jaas-krb5.conf
unzip "${jar_name}" krb5.conf

if [[ "${debug}" == "true" ]]; then
  export KRB5_TRACE=/dev/stderr
fi

java \
  -Djava.security.auth.login.config=jaas-krb5.conf \
  -Djava.security.krb5.conf=krb5.conf \
  -Djavax.security.auth.useSubjectCredsOnly=false \
  -Dsun.security.spnego.debug="${debug}" \
  -Dsun.security.krb5.debug="${debug}" \
  -jar "${jar_name}" $@