#!/usr/bin/env bash

sed -i -E "s+(.*?)entrypoint:.*+\1entrypoint: /usr/bin/env bash /entry_point.sh -d wdc_pgsql_v0.0.3:5432 -c '/usr/bin/env bash /run_wdc_core.sh'+" wdc.yml