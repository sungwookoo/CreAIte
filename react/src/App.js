import "./App.css";
import logo from "./assets/applogo.png";
import middleImage from "./assets/middle.png";
import oneDown from "./assets/one_downloadbadge_red_black.png";
import apkDown from "./assets/apkDownload.png";

function App() {
  return (
    <div className="app">
      <header className="headerStyle">
        <div>
          {/* <img src={logo} className="imageStyle" /> */}
          <p className="textStyle">CreAite</p>
        </div>
      </header>
      <hr />
      <div>
        <img src={middleImage} className="middleStyle" />
      </div>
      <br />
      <br />
      <div className="boxStyle">
        <a href="https://onesto.re/0000769538">
          <img src={oneDown} className="imageStyle" />
        </a>
        <div className="box"></div>
        {/* <a href="">
          <img src={apkDown} className="imageStyle" />
        </a> */}
        <img
          src={apkDown}
          onClick={() => {
            alert("준비중입니다!");
          }}
          className="imageReadyStyle"
        />
      </div>
      <br />
      <br />
    </div>
  );
}

export default App;
