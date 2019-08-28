new Vue({
    el: "#reg",
    data: {
        realName: "",
        gender: "",
        id: "",
        userName: "",
        password: "",
        password2: "",
        isread:false
    },
    methods: {
        check:function () {
            if (this.isread == false)
                alert("您还未同意相关服务条款和隐私政策！")
            else if(this.realName=="" || this.gender=="" || this.id=="" || this.userName=="" || this.password=="" || this.password2 == "")
                alert("有项目为空，请检查！")
            else if(this.password != this.password2)
                alert("两次输入的密码不一致！")
            else {
                axios({
                    url:'/Data/login',
                    params: {
                        param: {
                            Data:{
                                realName: this.realName,
                                gender: this.gender,
                                id: this.id,
                                userName: this.id,
                                password: this.password,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.status=="success"){
                            window.open("test.html"/* 登陆界面 */);
                            alert("注册成功！");
                        }
                        else{
                            alert("注册失败"+rep.reason)
                        }
                    }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                    })
            }
        }
    }
})