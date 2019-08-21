#!/bin/bash
if [ "$WDC_POSTGRES_USER" = "" ]; then
	export WDC_POSTGRES_USER="replica"
fi
if [ "$WDC_POSTGRES_PASSWORD" = "" ]; then
	export WDC_POSTGRES_PASSWORD="replica"
fi
psql -U $POSTGRES_USER --no-password -c "CREATE USER $WDC_POSTGRES_USER WITH PASSWORD '$WDC_POSTGRES_PASSWORD'"
psql -U $WDC_POSTGRES_USER --no-password -d postgres -f /tmp/ddl.sql 
