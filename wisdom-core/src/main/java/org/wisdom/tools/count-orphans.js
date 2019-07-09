const pg = require('pg')
const program = require('commander')

program
    .option("--host <string>", "pg host")
    .option("--port <number>", "pg port")
    .option("--username <string>", "pg username")
    .option("--password <string>", "pg password")
    .option("--database <string>", "pg database")


program.parse(process.argv)

const config = {
    user: program.username,
    database: program.database,
    password: program.password,
    port: program.port,

}

const pool = new pg.Pool(config)

// pool
//     .connect((err, client) => {
//         if(err != null){
//             console.error(err)
//             return
//         }
//         client.query("select (select count(*) from header) - (select max(height) + 1 from header) as orphan", (err, d) => {
//             console.log("the number of orphan blocks in db is " + d.rows[0].orphan)
//             client.release()
//         })
//     })

pool
    .connect()
    .then((client) => {
    return client.query("select (select count(*) from header) - (select max(height) + 1 from header) as orphan")
}).then((data) =>{
    console.log("the number of orphan blocks in db is " + data.rows[0].orphan)
})

pool
    .connect()
    .then((client) => {
        return client.query("select block_hash, height from header order by height desc limit 1")
    }).then((data) =>{
    console.log(`the best block hash is ${Buffer.from(data.rows[0].block_hash).toString('hex')} height is ${data.rows[0].height}`)
}).catch(console.error)


