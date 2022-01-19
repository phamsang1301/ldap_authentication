import { createSlice } from "@reduxjs/toolkit";

export const userSlice = createSlice({
	name: "user",
	initialState: {
		user: {
			username: "",
			isLoggedIn: false,
			roles: []
		}
	},
	reducers: {
		login: (state, action) => {
			state.user = action.payload;
		},
		logout: (state) => {
			state.user = {
				username: "",
				isLoggedIn: false,
				roles: []
			};
		}
	}
});

export const { login, logout } = userSlice.actions;
export const selectUser = (state) => state.authentication.user;
export default userSlice.reducer;