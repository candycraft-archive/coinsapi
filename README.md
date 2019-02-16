# CoinAPI

Simple API to save and retrieve coins by a player's UUID.

## Installation
Add this to your pom.xml:
```
<repository>
    <id>novusmc-repo</id>
    <url>http://116.203.16.116:8080/repository/internal/</url>
</repository>

<dependency>
    <groupId>de.pauhull</groupId>
    <artifactId>coinapi</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## Using the API

### Getting coins
```
CoinAPI.getInstance().getCoins(myUUID, coins -> {
	System.out.println(coins); // Print coins in console
});
```

### Setting coins
```
CoinAPI.getInstance().setCoins(myUUID, 12345);
```

### Checking if player has enough coins
```
CoinAPI.getInstance().hasEnoughCoins(myUUID, enoughCoins -> {
    if(enoughCoins) {
        System.out.println("You have enough coins.");
    }
});
```

## All available methods:

```
void CoinAPI#exists(String, Consumer<Boolean>)
void CoinAPI#exists(UUID, Consumer<Boolean>)
void CoinAPI#create(String)
void CoinAPI#create(UUID)
void CoinAPI#getCoins(String, Consumer<Integer>)
void CoinAPI#getCoins(UUID, Consumer<Integer>)
void CoinAPI#setCoins(String, int)
void CoinAPI#setCoins(UUID, int)
void CoinAPI#addCoins(String, int)
void CoinAPI#addCoins(UUID, int)
void CoinAPI#removeCoins(String, int)
void CoinAPI#removeCoins(UUID, int)
void CoinAPI#hasEnoughCoins(String, int, Consumer<Boolean>)
void CoinAPI#hasEnoughCoins(UUID, int, Consumer<Boolean>)
```