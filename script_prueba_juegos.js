//Para insertar juegos rapidamente,hay un endpoint para insertar los juegos de la forma correcta pero en caso de quere probar rapido puedes usar esto

db = connect("mongodb://localhost:27017/tableTop");

db.juegos.insertMany([
  { nombre: "Dungeons & Dragons" },
  { nombre: "Pathfinder" },
  { nombre: "Warhammer 40k" },
  { nombre: "Gloomhaven" },
  { nombre: "Magic: The Gathering" },
  { nombre: "Catan" },
  { nombre: "Carcassonne" },
  { nombre: "Terraforming Mars" },
  { nombre: "Twilight Imperium" },
  { nombre: "Blood Rage" }
]);

print("Juegos insertados correctamente.");