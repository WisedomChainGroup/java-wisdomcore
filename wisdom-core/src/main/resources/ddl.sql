create table if not exists transaction_index
(
    block_hash bytea not null,
    tx_hash bytea not null,
    tx_index integer,
    constraint transaction_index_pk
        primary key (block_hash, tx_hash)
);


create index if not exists transaction_index_block_hash
    on transaction_index (block_hash);

create table if not exists transaction
(
    version smallint,
    tx_hash bytea not null
        constraint transaction_pk
            primary key,
    type smallint,
    nonce bigint,
    "from" bytea,
    gas_price bigint,
    amount bigint,
    payload bytea,
    signature bytea,
    "to" bytea
);

create unique index if not exists transaction_tx_hash_uindex
    on transaction (tx_hash);

create table if not exists header
(
    block_hash bytea not null
        constraint header_pkey
            primary key,
    version bigint not null,
    hash_prev_block bytea not null,
    hash_merkle_root bytea not null,
    hash_merkle_state bytea not null,
    hash_merkle_incubate bytea,
        height bigint not null,
    created_at bigint not null,
    nbits bytea not null,
    nonce bytea not null,
    block_notice bytea,
    total_weight bigint default 0,
    is_canonical boolean default true
);

CREATE TABLE if not exists account (
                                    "id" bytea NOT NULL,
                                    "blockheight" int4 NOT NULL,
                                    "pubkeyhash" bytea NOT NULL,
                                    "nonce" int8 NOT NULL,
                                    "balance" int8 NOT NULL,
                                    "incubatecost" int8,
                                    "mortgage" int8,
                                    "vote" int8 DEFAULT 0 NOT NULL,
                                    constraint "pk_utxo" primary key ("id")
)
;

CREATE TABLE if not exists incubator_state (
                                            "id" bytea NOT NULL,
                                            "share_pubkeyhash" bytea,
                                            "pubkeyhash" bytea NOT NULL,
                                            "txid_issue" bytea NOT NULL,
                                            "height" int4 NOT NULL,
                                            "cost" int8 NOT NULL,
                                            "interest_amount" int8 NOT NULL,
                                            "share_amount" int8,
                                            "last_blockheight_interest" int4 NOT NULL,
                                            "last_blockheight_share" int4,
                                            constraint pk_incubator_state PRIMARY KEY ("id")
)
;


