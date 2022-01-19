import axios from "axios"
import { domain } from "../config/config"

export class logManagementService {
    getLogs = () => {
        return axios({
            url: `${domain}/admin/log`,
            method: "get",
			withCredentials: true
        })
    };
}

export const logManagementSvc = new logManagementService();