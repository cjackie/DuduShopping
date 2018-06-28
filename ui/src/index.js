import React from "react";
import ReactDOM from "react-dom";

const App = (props) => {
    return <div>hello world</div>;
};

const render = ({id}) => {
    ReactDOM.render(<App/>, document.getElementById(id));
};

export {render};