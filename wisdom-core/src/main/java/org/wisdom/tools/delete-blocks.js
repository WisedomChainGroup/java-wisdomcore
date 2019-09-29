#!/usr/bin/env node

/*
delete blocks starts from height h (inclusive)
删除（包含）某一高度以上的区块
示例
node delete-block.js --host localhost --port 5432 \
--username postgres --password postgres \
--database postgres --height 125852
 */

const pg = require('pg')
const program = require('commander')

program
    .option("--host <string>", "pg host")
    .option("--port <number>", "pg port")
    .option("--username <string>", "pg username")
    .option("--password <string>", "pg password")
    .option("--database <string>", "pg database")
    .option("--height <number>", "end block height")


program.parse(process.argv)


const config = {
    user: program.username || 'postgres',
    database: program.database || 'postgres',
    password: program.password || '',
    port: program.port || 5432,
    host: program.host || 'localhost'
}


const pool = new pg.Pool(config)


const HEIGHT = program.height

async function main() {
    const h = (await pool.query('select max(height) as height from header')).rows[0].height
    await pool.query(`
        delete from "transaction" where "transaction".tx_hash in (
            select ti.tx_hash from header as h inner join transaction_index as ti  
                on h.block_hash = ti.block_hash
            where h.height >= $1 and h.height <= $2
        ) 
    `, [HEIGHT, h])
    await pool.query(`delete from "transaction_index" where "transaction_index".block_hash in (
            select h.block_hash from header as h
                where h.height >= $1 and h.height <= $2
        )`, [HEIGHT, h])
    await pool.query(`delete from "header" as h where h.height >= $1 and h.height <= $2`, [HEIGHT, h])
    // 删掉 incubator
    await pool.query("delete from incubator_state where height >= $1", [HEIGHT])
    return await pool.query("delete from account where blockheight >= $1", [HEIGHT])
}

main().then(() => {
    console.log("success")
}).catch(console.error)
