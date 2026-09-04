$ErrorActionPreference='Stop'
$base='http://localhost:8080'
function Api($method,$path,$body,$token,$expected) {
    $args=@{Uri="$base$path";Method=$method;SkipHttpErrorCheck=$true;Headers=@{}}
    if($token){$args.Headers.Authorization="Bearer $token"}
    if($null -ne $body){$args.ContentType='application/json';$args.Body=$body|ConvertTo-Json -Depth 8}
    $r=Invoke-WebRequest @args
    if($r.StatusCode -ne $expected){throw "$method $path expected $expected got $($r.StatusCode): $($r.Content)"}
    Write-Host "$method $path => $expected"
    if($r.Content){return $r.Content|ConvertFrom-Json}
}
$email="customer-smoke-$([DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds())@example.com"
$password=[Guid]::NewGuid().ToString()
$user=Api POST /api/users @{name='Customer Smoke Test';email=$email;password=$password;phone='+971501234567';address='Dubai'} $null 201
$session=Api POST /api/login @{email=$email;password=$password} $null 200
$id=$user.customerId;$token=$session.token
$null=Api GET "/api/customers/$id/cart" $null $null 401
$null=Api GET /api/customers/7/cart $null $token 403
$null=Api POST "/api/customers/$id/cart/checkout" $null $token 400
$cart=Api POST "/api/customers/$id/cart/items" @{dishId=1;quantity=2} $token 200
if($cart.items.Count -ne 1){throw 'Cart item missing'}
$order=Api POST "/api/customers/$id/cart/checkout" $null $token 201
if($order.customerId -ne $id -or $order.items.Count -ne 1){throw 'Wrong order contents'}
$null=Api GET "/api/orders/$($order.orderId)" $null $token 200
$cart=Api GET "/api/customers/$id/cart" $null $token 200
if($cart.items.Count -ne 0){throw 'Cart not cleared'}
$null=Api POST /api/orders @{customerId=7;restaurantId=1;items=@(@{dishId=1;quantity=1})} $token 403
$null=Api POST /api/orders @{restaurantId=1;items=@(@{dishId=1;quantity=1})} $token 201
Write-Host "PASS: test customer $id, checkout order $($order.orderId); test records retained."
