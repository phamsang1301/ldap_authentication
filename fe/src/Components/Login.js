import React, { useState } from 'react';
import { Container, Row, Form, Button } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { Redirect } from 'react-router';
import swal from 'sweetalert';
import { login, selectUser } from '../features/userSlice';
import { authSvc } from '../services/authService';
import "../assets/css/Login.css"

const Login = () => {
	const user = useSelector(selectUser);

	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");

	const dispatch = useDispatch();

	const handleSubmit = (e) => {
		e.preventDefault();
		authSvc.auth(username, password).then(res => {
			console.log(res.status, res.data);
			swal("Successfully", "Login successfully!", "success", {
				buttons: false,
				timer: 1500,
			});
			dispatch(login({
				username: username,
				isLoggedIn: true,
				roles: res.data.split(",")
			}));
		}).catch(error => {
			console.log("error", error.response);
			if (error.response.status === 401) {
				swal("Failed", "Username or password is not correct!", "error", {
					buttons: false,
					timer: 1500,
				});
			} else {
				swal("Failed", "Something wrong!", "error", {
					buttons: false,
					timer: 1500,
				});
			}
			dispatch(login({
				username: username,
				isLoggedIn: false
			}));
		});
	};

	if (user.isLoggedIn) {
		if (user.roles.includes("admin")) {
			return <Redirect to="/admin" push />
		} else if (user.roles.includes("user")) {
			return <Redirect to="/user" push />
		} else {
			return <Redirect to="/" push />
		}
	} else {
		return (
			<Container fluid className="my-5">
				<Row className="text-center">
					<h1>Login Here</h1>
				</Row>
				<Row className="login">
					<Form onSubmit={handleSubmit}>
						<Form.Group size="lg" controlId="username">
							<Form.Label>Username</Form.Label>
							<Form.Control
								autoFocus
								type="text"
								placeholder="Enter username"
								onChange={(e) => setUsername(e.target.value)}
							/>
						</Form.Group>
						<Form.Group className="my-3" size="lg" controlId="password">
							<Form.Label>Password</Form.Label>
							<Form.Control
								type="password"
								placeholder="Enter password"
								onChange={(e) => setPassword(e.target.value)}
							/>
						</Form.Group>
						<Form.Group size="lg" controlId="submit">
							<Button size="lg" type="submit">
								Login
							</Button>
						</Form.Group>
					</Form>
				</Row>
			</Container>
		)
	}
}

export default Login