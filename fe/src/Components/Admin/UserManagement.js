import React, { useEffect, useState } from "react";
import {
  Container,
  Table,
  Row,
  Col,
  Modal,
  Form,
  Button,
} from "react-bootstrap";
import { useDispatch, useSelector } from "react-redux";
import { logout, selectUser } from "../../features/userSlice";
import { userManagementSvc } from "../../services/userManagementService";
import { Redirect } from "react-router-dom";
import swal from "sweetalert";
import LinkButton from "../LinkButton";
import "../../assets/css/UsrManagement.css";

export default function UserManagement() {
  const user = useSelector(selectUser);
  const dispatch = useDispatch();
  const [users, setUsers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newUser, setNewUser] = useState({
    values: {
      uid: "",
      userPassword: "",
      cn: "",
      givenName: "",
      sn: "",
      gender: "",
      mail: "",
    },
  });
  const [uidSearch, setUidSearch] = useState("");

  const AdminAction = { NONE: 0, ADD: 1, UPDATE: 2, DELETE: 3 };
  Object.freeze(AdminAction);
  const [act, setAct] = useState(AdminAction.NONE);

  useEffect(() => {
    userManagementSvc
      .getUsers()
      .then((res) => {
        setUsers(res.data);
      })
      .catch((error) => {
        console.log("error", error.response);
      });
  }, []);

  const renderUsers = () => {
    return users.map((usr, index) => {
      return (
        <tr key={index}>
          <td>{index + 1}</td>
          <td>{usr.uid}</td>
          <td>{usr.userPassword}</td>
          <td>{usr.cn}</td>
          <td>{usr.givenName}</td>
          <td>{usr.sn}</td>
          <td>{usr.gender}</td>
          <td>{usr.mail}</td>
          <td>
            <Button
              className="mb-2"
              variant="warning"
              onClick={() => {
                setAct(AdminAction.UPDATE);
                setNewUser({ values: usr });
                openModal();
              }}
            >
              Update
            </Button>
            <Button
              variant="danger"
              onClick={() => {
                setAct(AdminAction.DELETE);
                setNewUser({ values: usr });
                openModal();
              }}
            >
              Delete
            </Button>
          </td>
        </tr>
      );
    });
  };

  const openModal = () => {
    setShowModal(true);
  };

  const closeModal = () => {
    setAct(AdminAction.NONE);
    setShowModal(false);
    setNewUser({
      values: {
        uid: "",
        userPassword: "",
        cn: "",
        givenName: "",
        sn: "",
        gender: "",
        mail: "",
      },
    });
  };

  const handleChange = (event) => {
    let { value, name } = event.target;
    let newValues = {
      ...newUser.values,
    };
    newValues = { ...newValues, [name]: value };
    setNewUser({ values: newValues });
  };

  const handleChangeSearch = (event) => {
    setUidSearch(event.target.value);
  };

  const successfulSubmit = (res) => {
    closeModal();
    swal(
      "Successfully",
      (() => {
        switch (act) {
          case AdminAction.ADD:
            return "Add user successfully!";
          case AdminAction.UPDATE:
            return "Update user successfully!";
          case AdminAction.DELETE:
            return "Delete user successfully!";
          default:
            return "Something wrong!";
        }
      })(),
      "success",
      {
        buttons: false,
        timer: 1500,
      }
    );
    userManagementSvc
      .getUsers()
      .then((res) => {
        setUsers(res.data);
      })
      .catch((error) => {
        console.log("error", error.response);
      });
  };

  const failedSubmit = (error) => {
    console.log(error.response);
    if (error.response.status === 500) {
      swal("Failed", "Internal server error!", "error", {
        buttons: false,
        timer: 1500,
      });
    } else {
      swal("Failed", "Something error!", "error", {
        buttons: false,
        timer: 1500,
      });
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    let { values } = newUser;
    switch (act) {
      case AdminAction.ADD:
        userManagementSvc
          .addUser(values)
          .then((res) => {
            successfulSubmit(res);
          })
          .catch((error) => {
            failedSubmit(error);
          });
        break;
      case AdminAction.UPDATE:
        userManagementSvc
          .updateUser(values)
          .then((res) => {
            successfulSubmit(res);
          })
          .catch((error) => {
            failedSubmit(error);
          });
        break;
      case AdminAction.DELETE:
        userManagementSvc
          .deleteUser(values.uid)
          .then((res) => {
            successfulSubmit(res);
          })
          .catch((error) => {
            failedSubmit(error);
          });
        break;

      default:
        console.log("Something wrong!");
        return;
    }
  };

  const handleLogout = () => {
    dispatch(logout());
  };

  const handleSearch = (e) => {
    e.preventDefault();
    userManagementSvc
      .searchUser(uidSearch)
      .then((res) => {
        console.log(res.data);
        setUsers([res.data]);
      })
      .catch(() => {
        swal("Failed", "User does not exist!", "error", {
          buttons: false,
          timer: 1500,
        });
      });
  };

  const clearSearch = () => {
    document.getElementById("uidSearch").value = "";
    setUidSearch("");
    userManagementSvc
      .getUsers()
      .then((res) => {
        setUsers(res.data);
      })
      .catch((error) => {
        console.log("error", error.response);
      });
  };

  if (!user.isLoggedIn) {
    return <Redirect to="/login" push />;
  } else {
    return (
      <Container className="my-5">
        <h1 className="text-center my-5">User Management</h1>
        <Row className="text-center mb-3">
          <Col>
            <Button
              className="mt-4"
              style={{ width: "50%" }}
              onClick={(e) => {
                setAct(AdminAction.ADD);
                openModal();
              }}
            >
              Add user
            </Button>
          </Col>

          <Col>
            <Form className="ms-5 ps-5" onSubmit={handleSearch}>
              <Row>
                <Form.Group
                  controlId="uidSearch"
                  className=""
                  style={{ width: "50%" }}
                >
                  <Form.Label></Form.Label>
                  <Form.Control
                    type="text"
                    name="uid"
                    placeholder="Enter username"
                    onChange={handleChangeSearch}
                  />
                </Form.Group>
                <Button
                  variant="primary"
                  type="submit"
                  className="mt-4"
                  style={{ width: "15%" }}
                >
                  Search
                </Button>
                <Button
                  variant="secondary"
                  type="button"
                  className="mt-4 ms-2"
                  style={{ width: "15%" }}
                  onClick={clearSearch}
                >
                  Clear
                </Button>
              </Row>
            </Form>
          </Col>
        </Row>

        <Table striped bordered hover>
          <thead>
            <tr>
              <th>No.</th>
              <th>Username</th>
              <th>Password</th>
              <th>Full name</th>
              <th>Given name</th>
              <th>Surname</th>
              <th>Gender</th>
              <th>Email</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{renderUsers()}</tbody>
        </Table>

        <Modal show={showModal} onHide={closeModal}>
          <Modal.Header closeButton>
            <Modal.Title>
              {(() => {
                switch (act) {
                  case AdminAction.ADD:
                    return "Add user";
                  case AdminAction.UPDATE:
                    return "Update user";
                  case AdminAction.DELETE:
                    return "Delete user";
                  default:
                    return "";
                }
              })()}
            </Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <Form onSubmit={handleSubmit}>
              {act === AdminAction.DELETE ? (
                <p>
                  Are you sure to delete user <em>{newUser.values.uid}</em>?
                </p>
              ) : (
                <div>
                  <Form.Group controlId="uid" className="mb-3">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      name="uid"
                      defaultValue={newUser.values.uid}
                      disabled={act === AdminAction.UPDATE}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group controlId="password" className="my-3">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                      required
                      type="password"
                      name="userPassword"
                      defaultValue={newUser.values.userPassword}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group controlId="cn" className="my-3">
                    <Form.Label>Full name</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      name="cn"
                      defaultValue={newUser.values.cn}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group controlId="givenName" className="my-3">
                    <Form.Label>Given name</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      name="givenName"
                      defaultValue={newUser.values.givenName}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group controlId="sn" className="my-3">
                    <Form.Label>Surname</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      name="sn"
                      defaultValue={newUser.values.sn}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group className="my-3">
                    <Form.Label>Gender</Form.Label>
                    <Form.Check
                      required
                      type="radio"
                      name="gender"
                      value="male"
                      label="Male"
                      checked={newUser.values.gender === "male"}
                      onChange={handleChange}
                    />
                    <Form.Check
                      type="radio"
                      name="gender"
                      value="female"
                      label="Female"
                      checked={newUser.values.gender === "female"}
                      onChange={handleChange}
                    />
                  </Form.Group>
                  <Form.Group controlId="email" className="my-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control
                      required
                      type="email"
                      name="mail"
                      defaultValue={newUser.values.mail}
                      onChange={handleChange}
                    />
                  </Form.Group>
                </div>
              )}
              <Button variant="primary" type="submit" className="my-3">
                Submit
              </Button>
            </Form>
          </Modal.Body>
        </Modal>

        <Row className="text-center">
          <Col>
            <LinkButton to="/admin" style={{ width: "50%" }}>
              Back to Admin Homepage
            </LinkButton>
          </Col>
          <Col>
            <LinkButton
              to="/admin"
              style={{ width: "50%" }}
              onClick={handleLogout}
            >
              Logout
            </LinkButton>
          </Col>
        </Row>
      </Container>
    );
  }
}
