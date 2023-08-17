import GetCustomer from "./scenarios/auth-user.js";
import {group, sleep} from 'k6';

export default () => {
    group("Endpoint Auth User", () => {
        GetCustomer();
    });
    sleep(1);
}