<%@ page import="java.io.File" %>
<%@ page import="com.github.b402.cmc.core.FileManager" %>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios@0.12.0/dist/axios.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/lodash@4.13.1/lodash.min.js"></script>
</head>
<body>
<div id="app">
    <p>{{ message }}</p>
</div>
<script>
    var v = new Vue({
        el: '#app',
        data: {
            message: ''
        }
    })
</script>
</body>
</html>