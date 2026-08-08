import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    iterations: 100,
};

export default function () {

    const response = http.post(
        'http://localhost:8080/api/v1/inventory/1/reserve?quantity=1'
    );

    check(response, {
        'request completed': (r) =>
            r.status === 200,
    });
}