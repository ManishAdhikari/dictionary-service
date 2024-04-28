db.createUser({
  user: 'adminUser',
  pwd: 'adminPass',
  roles: [
    {
      role: 'readWrite',
      db: 'dictionary-service-db',
    },
  ],
});
db.auth('adminUser', 'adminPass');
db = db.getSiblingDB('dictionary-service-db');
db.createCollection('dictionary', { capped: false });
db.createUser(
  {
    user: "dictUser",
    pwd: "dictPass",
    roles: [
        {
            role: "readWrite",
            db: "dictionary-service-db"
        }
    ]
  }
);
