import axios from "axios";
import { domain } from "../config/config";

export class userService {
  getUser = (uid) => {
    return axios({
      url: `${domain}/user?uid=${uid}`,
      method: "get",
      withCredentials: true,
    });
  };
}

export const userSvc = new userService();
