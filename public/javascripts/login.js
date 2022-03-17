const loginButton = () => {
    const username = $('#loginUsername').val();
    const password = $('#loginPassword').val();
    var json = {
       "username": username,
       "password": password,
    }
    $.ajax({
        url: `api/v1/login`,
        type: 'POST',
        data: JSON.stringify(json),
        contentType: 'application/json; charset=utf-8',
        cache: false,
        dataType: "json",
        success: function(response){
            console.log(response)
            window.location.href = "/";
        }
    })
}