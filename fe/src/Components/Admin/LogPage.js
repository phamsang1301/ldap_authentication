import React, { useEffect, useState } from 'react'
import { Container, Table, Row, Col } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { logout, selectUser } from '../../features/userSlice';
import { logManagementSvc } from "../../services/logManagementService";
import { authSvc } from "../../services/authService";
import { Redirect } from "react-router-dom";
import LinkButton from '../LinkButton';
import '../../assets/css/UsrManagement.css';

export default function LogPage() {
	const user = useSelector(selectUser);
	const dispatch = useDispatch();
	const [logs, setLogs] = useState([]);

	useEffect(() => {
		logManagementSvc.getLogs().then(res => {
			setLogs(res.data);
		}).catch(error => {
			console.log("error", error.response);
		});
	}, []);

	const renderLogs = () => {
		return logs.map((logs, index) => {
			return <tr key={index}>
				<td>{logs.date}</td>
				<td>{logs.time}</td>
				<td>{logs.action}</td>
				<td>{logs.description}</td>
				<td>{logs.admin_uid}</td>
			</tr>
		})
	};

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
					<h1 className="text-center my-5">Server Log</h1>
					<Row>
						<Table striped bordered hover>
							<thead>
								<tr>
									<th>Date</th>
									<th>Time</th>
									<th>Action</th>
									<th>Description</th>
									<th>Admin Id</th>
								</tr>
							</thead>
							<tbody>
								{renderLogs()}
							</tbody>
						</Table>
					</Row>


					<Row className="text-center">
						<Col>
							<LinkButton to="/admin" style={{ width: "50%" }}>Back to Admin Homepage</LinkButton>
						</Col>
						<Col>
							<LinkButton to="/" style={{ width: "50%" }} onClick={handleLogout}>Logout</LinkButton>
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
