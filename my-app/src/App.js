import logo from './logo.svg';
import './App.css';
import AIComponent from './components/AIComponent';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>AI Content Generator</h1>
        <AIComponent />
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
