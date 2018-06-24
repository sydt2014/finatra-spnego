#!/usr/bin/env bash

debug=true

if [[ "${debug}" == "true" ]]; then
  export KRB5_TRACE=/dev/stderr
fi

java \
  -Djava.security.auth.login.config=jaas-krb5.conf \
  -Djava.security.krb5.conf=krb5.conf \
  -Djavax.security.auth.useSubjectCredsOnly=false \
  -Dsun.security.spnego.debug="${debug}" \
  -Dsun.security.krb5.debug="${debug}" \
  -jar $@