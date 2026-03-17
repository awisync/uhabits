create table Reminders (
    id integer primary key autoincrement,
    habit integer references Habits(id),
    hour integer,
    min integer,
    days integer
);
