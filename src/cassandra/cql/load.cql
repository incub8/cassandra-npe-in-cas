create keyspace testingkeyspace
with replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

create table testingkeyspace.texts (
    key text primary key,
    content text
);

create table testingkeyspace.lists (
    key text primary key,
    content list<text>
);

create table testingkeyspace.sets (
    key text primary key,
    content set<text>
);

create table testingkeyspace.maps (
    key text primary key,
    content map<text, text>
);

create table testingkeyspace.frozenlists (
    key text primary key,
    content frozen<list<text>>
);

create table testingkeyspace.frozensets (
    key text primary key,
    content frozen<set<text>>
);

create table testingkeyspace.frozenmaps (
    key text primary key,
    content frozen<map<text, text>>
);