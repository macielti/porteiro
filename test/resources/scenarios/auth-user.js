import http from 'k6/http';
import { sleep } from 'k6';
import { check, fail } from 'k6';

export default function () {
    const payload = JSON.stringify({
        username: 'admin',
        password: '@1udadG5gH4#',
        });

    const headers = { 'Content-Type': 'application/json' };
    let res = http.post('https://porteiro.nullab.com.br/api/customers/auth', payload, { headers });

    let durationMsg = 'Falha na execução do cenário de teste news';

    if(!check(res, {
        'is statuscode 200 - endpoint news': (r) => r.status === 200
    })){
        console.log(res);
        fail(res.status, res.payload);
    }

    sleep(1);

}
