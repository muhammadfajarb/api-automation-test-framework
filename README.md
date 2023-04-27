### API Automation Test Framework

Objective task pada project ini adalah untuk melakukan automation task pada api https://reqres.in.
Framework testing yang digunakan adalah TestNG, HTTP client library menggunakan Rest-Assured, dan report menggunakan Allure.

Terdapat sebanyak 11 test yang dilakukan. Hasil report menggunakan Allure adalah sebagai berikut:
![report](screenshot1.png)
![report](screenshot2.png)

Seluruh pengujian endpoint pada method GET, POST, PUT, PATCH, dan DELETE termasuk positive dan negative test berhasil dilakukan kecuali pada boundaries test di mana yang diuji adalah batasan minimal dan maksimal halaman. Hasil yang diharapkan adalah api mampu memberikan status code 400 jika query parameter page yang diberikan tidak sesuai dengan batasan dari data yang tersedia, namun api tetap memberikan status code 200 sehingga pengujian endpoint pada test boundaries tidak berhasil.