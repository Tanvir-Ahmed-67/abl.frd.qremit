(() => {
    for (var e = document.querySelectorAll(".sidebar-item.has-sub"), t = function () {
        var t = e[r];
        e[r].querySelector(".sidebar-link").addEventListener("click", (function (e) {
            e.preventDefault();
            var r = t.querySelector(".submenu");
            r.classList.contains("active") ? r.classList.remove("active") : r.classList.add("active")
        }))
    }, r = 0; r < e.length; r++) t();
    var a = document.querySelectorAll(".sidebar-toggler");
    for (r = 0; r < a.length; r++) {
        a[r].addEventListener("click", (function () {
            var e = document.getElementById("sidebar");
            e.classList.contains("active") ? e.classList.remove("active") : e.classList.add("active")
        }))
    }
    if ("function" == typeof PerfectScrollbar) {
        var c = document.querySelector(".sidebar-wrapper");
        new PerfectScrollbar(c)
    }
    window.onload = function () {
        var e = window.innerWidth;
        e < 768 && (console.log("widthnya ", e), document.getElementById("sidebar").classList.remove("active"))
    }, feather.replace()
})();
var check = function() {
    if (document.getElementById('password').value ==
        document.getElementById('confirm-password').value) {
        document.getElementById('message').style.color = 'green';
        document.getElementById('message').innerHTML = 'Password matched!';
    } else {
        document.getElementById('message').style.color = 'red';
        document.getElementById('message').innerHTML = 'Password did not match!';
    }
}
function validateCreditCardForm(){
    var result = false;
    if (($('#userName').val().length > 0) &&
        ($('#userEmail').val().length  > 0) &&
        ($('#userNrtaCode').val().length > 0) &&
        ($('#userType').val().length > 0) &&
        ($('#userPassword').val().length > 0) &&
        ($('#userRetypePassword').val().length > 0)) {
        result = true;
    }
    return result;
}