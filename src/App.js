import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import CaseManagerPatients from './main/webapp/CaseManagement/CaseManagerPatientsList';

import HomePage from './main/webapp/CaseManagement/Dashboard'

export default function App() {
  return (
    <Router>
      <div>
       
        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
        <Switch>
          <Route path="/dispatched-sample">
            <CaseManagerPatientsList />
          </Route>
          
          <Route path="/">
            <Home />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

function Home() {
  return <HomePage />;
}

function CaseManagerPatientsList() {
  return <CaseManagerPatients />;
}

