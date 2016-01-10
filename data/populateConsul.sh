#!/bin/bash

curl -X PUT -d value http://127.0.0.1:8500/v1/kv/prod/gets/key
curl -X PUT -d value2 http://127.0.0.1:8500/v1/kv/prod/gets/key2
curl -X PUT -d firstvalue http://127.0.0.1:8500/v1/kv/prod/gets/anotherkey
curl -X PUT -d secondvalue http://127.0.0.1:8500/v1/kv/prod/gets/anotherkey
curl -X PUT -d thirdvalue http://127.0.0.1:8500/v1/kv/prod/gets/anotherkey
