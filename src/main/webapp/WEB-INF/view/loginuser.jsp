<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Payment System</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous"/>
</head>
<body>
<div class="container">
    <form class="form-signin" method="post" action="/authenticatelogin">
        <h1 class="form-signin-heading">Welcome to Payment System</h1>
        <p>
            <label for="usermail" class="sr-only">Username</label>
            <input type="text" id="usermail" name="usermail" class="form-control" placeholder="Username" required autofocus>
        </p>
        <p>
            <label for="userpassword" class="sr-only">Password</label>
            <input type="userpassword" id="userpassword" name="userpassword" class="form-control" placeholder="Password" required>
        </p>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
    </form>
</body>
</html>