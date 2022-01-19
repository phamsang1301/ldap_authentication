import React from 'react'
import { Container, Row, Col } from 'react-bootstrap';
import LinkButton from '../LinkButton';

const PublicPage = () => {
	return (
		<Container className="my-5">
			<Row className="text-center my-3">
				<h1>Public Page</h1>
			</Row>
			<Row className="text-center my-3">
				<Col>
					<LinkButton to="/login" style={{ width: "25%" }}>
						Login
					</LinkButton>
				</Col>
			</Row>
		</Container>
	)
}

export default PublicPage
