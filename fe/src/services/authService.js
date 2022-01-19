import axios from "axios"
import { domain } from "../config/config"

export class authService {
	auth = (username, password) => {
		var fd = new FormData()
        fd.append('username', username)
        fd.append('password', password)

		return axios({
			url: `${domain}/login`,
			method: 'post',
			headers: { 'Content-Type': 'multipart/form-data' },
			data: fd,
			withCredentials: true
		})
	}

	logout = () => {
		return axios({
			url: `${domain}/logout`,
			method: 'get',
			withCredentials: true
		})
	}
}

export const authSvc = new authService();