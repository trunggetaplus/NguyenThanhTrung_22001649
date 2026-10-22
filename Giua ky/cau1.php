<?php
$name = "Doan Binh";
$score = 8.5;

if ($score >= 8) {
    $rank = "Gioi";
} elseif ($score >= 6.5) {
    $rank = "Kha";
} elseif ($score >= 5) {
    $rank = "Trung binh";
} else {
    $rank = "Yeu";
}

echo "Ten hoc sinh: " . $name . " | ";
echo "Diem: " . $score . " | ";
echo "Xep loai: " . $rank;
?>