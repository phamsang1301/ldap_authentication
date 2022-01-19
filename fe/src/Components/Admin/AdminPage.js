import React from 'react'
import { Container, Row, Col } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { Redirect } from 'react-router-dom';
import { logout, selectUser } from '../../features/userSlice';
import { authSvc } from '../../services/authService';
import LinkButton from '../LinkButton';
import "../../assets/css/AdminPage.css"

const AdminPage = () => {
	const user = useSelector(selectUser);
	const dispatch = useDispatch();

	const handleLogout = () => {
		authSvc.logout().then(res => {
			console.log(res.status, res.data);
		})
		dispatch(logout());
	};

	if (!user.isLoggedIn) {
		return <Redirect to="/login" push />
	} else {
		if (user.roles.includes("admin")) {
			return (
				<Container className="my-5">
					<Row className="text-center my-3">
						<h1>Admin Page</h1>
						<p className="fs-5">Welcome <em>{user.username}</em>!</p>
					</Row>
					<Row className="text-center my-3">
						<Col>
							<LinkButton to="/user">
								My account
							</LinkButton>
						</Col>
						<Col>
							<LinkButton to="/admin/user-management">
								User management
							</LinkButton>
						</Col>
						<Col>
							<LinkButton to="/admin/log">
								Log
							</LinkButton>
						</Col>
						<Col>
							<LinkButton to="/" onClick={handleLogout}>
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
						<h1>You are not the administrator!</h1>
					</Row>
				</Container>
			)
		}
	}
}

export default AdminPage
