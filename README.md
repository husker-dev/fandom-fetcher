<h1>
Fandom Fetcher
</h1>

## ```Описание```

  Инструмент для поиска участников фандома, субкультуры, сообщества в своём городе с использованием VK.
    
  Программа использует самописную библиотеку для доступа к VK Api. Она 
  позволяет получать большие даные за максимально короткое время.  

## ```Скриншоты```

<div>

<img src="https://i.ibb.co/JzvZYzg/image.png" width="200">
<img src="https://i.ibb.co/sbdKqn9/image.png" width="200">
<img src="https://i.ibb.co/2Zkcdg8/image.png" width="200">
<img src="https://i.ibb.co/TH5rxpF/image.png" width="200">

</div>

---

## ```Скачать```

- Windows/Linux/MacOS (x64):  [```Скачать```](https://github.com/husker-dev/fandom-fetcher/releases/download/1.0/SubCultureFetcher-1.0-x64.jar)

- Windows/Linux/MacOS (x86):  [```Скачать```](https://github.com/husker-dev/fandom-fetcher/releases/download/1.0/SubCultureFetcher-1.0-x86.jar)

## ```Требования```

- Java 11 или новее:  [```Установить```](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)

- Аккаунт VK

- Неограниченный интернет

---

## ```Возможные вопросы```

<details>
  <summary>Как оно работает?</summary>

---
  
Приложение получает список всех участников разнообразных групп, и отбирает пользователей по следующим критериям: 
- ```Город```
- ```Место учёбы```
- ```Родной город```

Так же берутся общие участники между группами, и городскими сообществами (беседки, подслушано и т.д.)

Все данные получаются в процессе через VK API (ничего не сохраняется).

---
</details>

<details>
  <summary>Почему требуется вход в VK?</summary>

---
  
VK API требует хоть какой-нибудь аккаунт, что бы получить ключ доступа.

В целях безопасности в код проекта не входит заготовленный ключ, поэтому требуется вход при каждом запуске.

---
</details>

<details>
  <summary>Есть ли ограничения?</summary>

---

Да, есть.

VK API ограничивает вызов одинаковых методов, которые активно используются в приложении.

Обычно лимита хватает на 2-3 проверки в день, в зависимости от размеров города.
По исчерпании лимита показывается предупреждение. В таком случае можно поменять аккаунт при входе в VK.

---
</details>
