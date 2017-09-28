#!/bin/bash
exec bin/webapp  \
 -Dplay.http.secret.key='secret-key' \
 -Dplay.server.http.port=80
