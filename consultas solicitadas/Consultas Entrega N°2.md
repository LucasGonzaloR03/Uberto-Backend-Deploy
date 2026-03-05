## Punto 1: Saber qué chofer es el más clickeado
```js
db.ContadorClick.aggregate([
 {
   $group: {
     _id: "$choferid",
     nombreChofer: { $first: "$nombreChofer" },
     total: { $sum: 1 }
   }
 },
 { $sort: { total: -1 } },
 { $limit: 1 }
])
```

## Punto 2: Saber cuántos choferes son del tipo moto.
```js
db.Chofer.countDocuments({ _class: "CMOTO" })
```

## Punto 3: Saber qué choferes tienen más de 4 puntos de calificación.
```js
db.Chofer.find({ promedioDePuntaje: { $gt: 4 } })
```

## Punto 4: Saber qué choferes tienen un costo base entre $1.000 y $5.000.
```js
db.Chofer.find({ precioBase: { $gte: 1000, $lte: 5000 } })
```

## Punto 5: Saber qué choferes tienen todos los viajes terminados.
```js
db.Chofer.find({
  "$and": [
    { "viajesDelChofer": { "$ne": [] } },
    { "viajesDelChofer": { "$not": { "$elemMatch": { "fechaFinalizacion": { "$gt": new Date() } } } } }
  ]
})
```