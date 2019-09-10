//获取cookie函数
function getCookie(name){
    var aCookie = document.cookie.split("; ");
    for(var i =0;i<aCookie.length;i++){
        var keyValue = aCookie[i].split("=");
        if(name==keyValue[0]) {
            return keyValue[1];
        }
    }
    return null;
}


function check(){
    if(!getCookie("token")){
        alert("请先登录");
        window.location.href = "loginX.html";
    }
}

check();

/*
//获取作为选手参加的比赛
function joinCompetitions(){
    axios({
        //待定
        url: '',
        params:{
            param:{
                token: getCookie("token"),
                Data : {


                }
            }
        }
    }).then()           //对对象赋值 用 v-for 渲染
}

//获取作为裁判参加的比赛
function judgeCompetitions(){
    axios({
        //待定
        url: '',
        params:{
            param:{
                token: getCookie("token"),
                Data : {


                }
            }
        }
    }).then()
}

*/