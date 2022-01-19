import { BrowserRouter, Route, Switch } from 'react-router-dom'
import UserPage from './Components/User/UserPage';
import PublicPage from './Components/User/PublicPage';
import AdminPage from './Components/Admin/AdminPage';
import LogPage from './Components/Admin/LogPage';
import UserManagement from './Components/Admin/UserManagement';
import Login from './Components/Login';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
	return (
		<BrowserRouter>
			<Switch>
				<Route exact path='/' component={PublicPage} />
				<Route exact path='/login' component={Login} />
				<Route exact path='/user' component={UserPage} />
				<Route exact path='/admin' component={AdminPage} />
				<Route exact path='/admin/user-management' component={UserManagement} />
				<Route exact path='/admin/log' component={LogPage} />
			</Switch>
		</BrowserRouter>
	);
}

export default App;
