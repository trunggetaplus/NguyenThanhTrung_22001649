<?php
$prices = [100, 250, 150, 500, 300];

function calculateTotal($prices) {
    $total = 0;

    foreach ($prices as $price) {
        $total += $price;
    }

    return $total;
}

function findMaxPrice($prices) {
    $max = $prices[0];

    foreach ($prices as $price) {
        if ($price > $max) {
            $max = $price;
        }
    }

    return $max;
}

$total = calculateTotal($prices);
$maxPrice = findMaxPrice($prices);

echo "Tong gia tri san pham: " . $total . "<br>";
echo "Gia san pham cao nhat: " . $maxPrice;
?>