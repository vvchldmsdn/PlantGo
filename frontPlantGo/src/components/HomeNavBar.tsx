import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import logo from "../img/플랜트고-색.png";
import { IoIosArrowBack } from "react-icons/io";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { useState } from "react";
import Image from "react-bootstrap/Image";
import Button from "react-bootstrap/Button";

import {
  BsCameraFill,
  BsPlus,
  BsBook,
  BsAward,
  BsBoxArrowRight,
  BsArrowRepeat,
} from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import "./HomeNavBar.css";
import "./KakaoMap.css";

const HomeNavBar = () => {
  let Navigate = useNavigate();
  const [isShown, setIsShown] = useState(false);

  const toggleMenu = () => {
    setIsShown((current) => !current);
  };

  const reloadPage = () => {
    window.location.reload();
  };

  const logOut = () => {
    sessionStorage.clear();
    Navigate("/login");
  };
  return (
    <div className="kakao-map-buttons">
      {/* 추가메뉴 */}
      {isShown && (
        <Container>
          <Nav
            style={{
              position: "fixed",
              right: 0,
              zIndex: 1030,
              bottom: 130,
              marginRight: "5px",
            }}
          >
            <Nav.Item
              onClick={() => {
                Navigate("/rank");
              }}
            >
              <button className="btn btn-default" style={{padding:0}}>
                <BsAward
                  style={{
                    width: 50,
                    height: 50,
                    zIndex: 2,
                  }}
                ></BsAward>
              </button>
            </Nav.Item>
          </Nav>
          <Nav
            style={{
              position: "fixed",
              right: 0,
              zIndex: 1030,
              bottom: 195,
              marginRight: "5px",
            }}
          >
            <Nav.Item
              onClick={() => {
                Navigate("/plantlist");
              }}
            >
              <button className="btn btn-default" style={{padding:0}}>
                <BsBook
                  style={{
                    width: 50,
                    height: 50,
                    zIndex: 2,
                  }}
                ></BsBook>
              </button>
            </Nav.Item>
          </Nav>
          <Nav
            style={{
              position: "fixed",
              right: 0,
              zIndex: 1030,
              bottom: 65,
            }}
          >
            <Nav.Item onClick={logOut}>
              <button className="btn btn-default" style={{padding:0}}>
                <BsBoxArrowRight
                  style={{
                    width: 50,
                    height: 50,
                    zIndex: 2,
                  }}
                ></BsBoxArrowRight>
              </button>
            </Nav.Item>
          </Nav>
        </Container>
      )}

      {/* 상단메뉴 */}
      <button
        className="btn btn-default"
        style={{
          position: "fixed",
          top: "15px",
          right: "15px",
          zIndex: 2,
        }}
      >
        <BsArrowRepeat
          onClick={reloadPage}
          style={{
            width: 50,
            height: 50,
            position: "fixed",
            top: "15px",
            right: "15px",
            zIndex: 2,
          }}
        />
      </button>
      <Image
        src={logo}
        style={{
          width: 90,
          height: 90,
          position: "fixed",
          top: "15px",
          left: "15px",
          backgroundColor: "white",
          borderRadius: "50%",
          zIndex: 2,
        }}
      />

      {/* 하단메뉴 */}
      <Container>
        <div>
          <button
            className="btn btn-default"
            onClick={() => {
              Navigate("/camera");
            }}
            style={{
              position: "fixed",
              left: "50%",
              bottom: 0,
              marginLeft: "-25px",
              display: "block",
              zIndex: 2,
            }}
          >
            <BsCameraFill
              style={{
                width: 50,
                height: 50,
                zIndex: 2,
              }}
            />
          </button>
          <button
            onClick={toggleMenu}
            className="btn btn-default"
            style={{
              position: "fixed",
              right: 0,
              padding:0,
              bottom: 0,
              display: "block",
              zIndex: 2,
            }}
          >
            <BsPlus
              style={{
                width: 60,
                height: 60,
                zIndex: 2,
              }}
            />
          </button>
        </div>
      </Container>
    </div>
  );
};

export default HomeNavBar;
