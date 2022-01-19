import React, { useEffect, useState } from 'react'
import { Container, Row, Col, Table } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { Redirect } from 'react-router-dom';
import { logout, selectUser } from '../../features/userSlice';
import { authSvc } from '../../services/authService';
import { userSvc } from '../../services/userService';
import LinkButton from '../LinkButton';
import "../../assets/css/AdminPage.css"

const UserPage = () => {
	const user = useSelector(selectUser);
	const dispatch = useDispatch();
	const [usr, setUsr] = useState({
		values: {
			uid: "",
			userPassword: "",
			cn: "",
			givenName: "",
			sn: "",
			gender: "",
			mail: "",
		}
	})

	useEffect(() => {
		userSvc.getUser(user.username).then(res => {
			setUsr({ values: res.data });
		}).catch(error => {
			console.log("error", error.response);
		});
	}, [user.username]);

	const handleLogout = () => {
		authSvc.logout().then(res => {
			console.log(res.status, res.data);
		})
		dispatch(logout());
	};

	if (!user.isLoggedIn) {
		return <Redirect to="/login" push />
	} else {
		if (user.roles.includes("user")) {
			return (
				<Container className="my-5">
					<Row className="text-center my-3">
						<h1>User Page</h1>
						<p className="fs-5">Welcome <em>{user.username}</em>!</p>
					</Row>
					<Row>
						<Table striped bordered hover>
							<thead>
								<tr>
									<th>Attribute</th>
									<th>Value</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Username</td>
									<td>{usr.values.uid}</td>
								</tr>
								<tr>
									<td>Full name</td>
									<td>{usr.values.cn}</td>
								</tr>
								<tr>
									<td>Given name</td>
									<td>{usr.values.givenName}</td>
								</tr>
								<tr>
									<td>Surname</td>
									<td>{usr.values.sn}</td>
								</tr>
								<tr>
									<td>Gender</td>
									<td>{usr.values.gender}</td>
								</tr>
								<tr>
									<td>Email</td>
									<td>{usr.values.mail}</td>
								</tr>
							</tbody>
						</Table>
					</Row>
					<Row className="text-center my-3">
						{
							user.roles.includes("admin") ?
								<Col>
									<LinkButton to="/admin" style={{ width: "50%" }}>
										Back to Admin Page
									</LinkButton>
								</Col>
								: <Col></Col>
						}
						<Col>
							<LinkButton to="/" style={{ width: "50%" }} onClick={handleLogout}>
								Logout
							</LinkButton>
						</Col>
					</Row>
				</Container>
			)
		} else {
			return (
				<Container className="my-5">
					<Row className="text-center my-3">
						<h1>You are not the registered user!</h1>
					</Row>
				</Container>
			)
		}
	}
}

export default UserPage
