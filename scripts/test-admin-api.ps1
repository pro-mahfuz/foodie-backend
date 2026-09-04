param([string]$BaseUrl = 'http://localhost:8080', [Parameter(Mandatory)][string]$AdminPassword, [Parameter(Mandatory)][string]$CustomerPassword)
$ErrorActionPreference = 'Stop'
function Call-Api($Method, $Path, $Body, $Token, $Expected) {
    $headers = @{}
    if ($Token) { $headers.Authorization = "Bearer $Token" }
    $args = @{Uri="$BaseUrl$Path"; Method=$Method; Headers=$headers; SkipHttpErrorCheck=$true}
    if ($null -ne $Body) { $args.ContentType='application/json'; $args.Body=($Body | ConvertTo-Json -Depth 8) }
    $r = Invoke-WebRequest @args
    if ($r.StatusCode -ne $Expected) { throw "$Method $Path expected $Expected but got $($r.StatusCode): $($r.Content)" }
    Write-Host "$Method $Path => $Expected"
    if ($r.Content) { return ($r.Content | ConvertFrom-Json) }
}
$admin=Call-Api POST /api/login @{email='admin@gmail.com';password=$AdminPassword} $null 200
$customer=Call-Api POST /api/login @{email='customer@gmail.com';password=$CustomerPassword} $null 200
$restaurant=@{name='Admin API Smoke Test';address='Dubai';phone='+971501234567';rating=4.5}
$null=Call-Api POST /api/restaurants $restaurant $null 401
$null=Call-Api POST /api/restaurants $restaurant 'forged' 401
$null=Call-Api POST /api/restaurants $restaurant $customer.token 403
$r=Call-Api POST /api/restaurants $restaurant $admin.token 201
$restaurant.name='Admin API Smoke Test Updated'
$null=Call-Api PUT "/api/restaurants/$($r.restaurantId)" $restaurant $admin.token 200
$null=Call-Api GET /api/restaurants $null $null 200
$dish=@{name='Smoke Test Dish';description='Test data';price=12.50;category='MAIN'}
$d=Call-Api POST "/api/restaurants/$($r.restaurantId)/dishes" $dish $admin.token 201
$dish.price=13.50
$null=Call-Api PUT "/api/restaurants/$($r.restaurantId)/dishes/$($d.dishId)" $dish $customer.token 403
$null=Call-Api PUT "/api/restaurants/$($r.restaurantId)/dishes/$($d.dishId)" $dish $admin.token 200
$null=Call-Api GET "/api/restaurants/$($r.restaurantId)/dishes" $null $null 200
$dish.price=-1
$null=Call-Api POST "/api/restaurants/$($r.restaurantId)/dishes" $dish $admin.token 400
Write-Host "PASS. Test data retained: restaurantId=$($r.restaurantId), dishId=$($d.dishId)"
