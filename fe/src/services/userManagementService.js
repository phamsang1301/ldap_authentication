import axios from "axios";
import { domain } from "../config/config";

export class userManagementService {
  getUsers = () => {
    return axios({
      url: `${domain}/admin/users`,
      method: "get",
      withCredentials: true,
    });
  };

  addUser = (dataUser) => {
    return axios({
      url: `${domain}/admin/add-user`,
      method: "post",
      data: dataUser,
      withCredentials: true,
    });
  };

  updateUser = (dataUser) => {
    return axios({
      url: `${domain}/admin/update-user`,
      method: "put",
      data: dataUser,
      withCredentials: true,
    });
  };

  deleteUser = (uid) => {
    return axios({
      url: `${domain}/admin/delete-user?uid=${uid}`,
      method: "delete",
      withCredentials: true,
    });
  };

  searchUser = (uid) => {
    return axios({
      url: `${domain}/admin/search?uid=${uid}`,
      method: "get",
      withCredentials: true,
    });
  };
}

export const userManagementSvc = new userManagementService();
