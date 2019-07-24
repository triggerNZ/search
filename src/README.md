Search
======

A little interactive console program that demonstrates searching datasets using search indexes and their
composition.

Usage
-----

To run the program, assuming you have sbt installed:

```
sbt run
```

That will build the project, install dependencies and run.

You will be presented with a simple prompt:
```
> 
```
where search queries can be entered. We will cover query syntax shortly, but 
first as an example, find all users in the 'Zentry' organzation:

```
> users.orgName:Zentry
+----+---------------+---------------+----------------------------------------+
|Id  |Name           |Alias          |Tags                                    |
+----+---------------+---------------+----------------------------------------+
|30  |Debra William  |Mr Murray      |Chesterfield, Brutus, Echo, Valmy       |
|68  |Sweet Cain     |Mr Hyde        |Fairlee, Grandview, Fairview, Williston |
|70  |Nash Rivers    |Mr Alexandria  |Barclay, Odessa, Southmont, Lisco       |
+----+---------------+---------------+----------------------------------------+
> 
```

So three users were found, and some of their information was displayed.

Another example, let's find tickets submitted by Nash Rivers, but because we can,
search by his alias Mr Alexandria instead:

```
> tickets.submitter:Mr Alexandria
+--------------------------------------+----------------------------------------+---------------------------------------------+--------+--------+
|Id                                    |Subject                                 |Tags                                         |Priority|Type    |
+--------------------------------------+----------------------------------------+---------------------------------------------+--------+--------+
|1bdad283-b751-407d-a6d5-8067016b8010  |A Drama in Cocos (Keeling Islands)      |Virginia, Virgin Islands, Maine, West Virgini|Urgent  |Problem |
|3ff0599a-fe0f-4f8f-ac31-e2636843bcea  |A Problem in Antigua and Barbuda        |American Samoa, Northern Mariana Islands, Pue|Low     |Question|
|e0e5ab4a-a776-40ec-8768-64d83a342d82  |A Drama in Albania                      |South Carolina, Indiana, New Mexico, Nebraska|High    |Task    |
|945ce2d3-3edc-4936-8d51-e59e74cf917a  |A Drama in Guinea                       |American Samoa, Northern Mariana Islands, Pue|Urgent  |Task    |
|7a0b41db-f910-4814-8d75-1e0915ec5d27  |A Drama in Bolivia                      |Puerto Rico, Idaho, Oklahoma, Louisiana      |Normal  |Question|
+--------------------------------------+----------------------------------------+---------------------------------------------+--------+--------+
```

Query Syntax
------------

Queries are in the form `<dataset>.<index>:<query>`, where

- `dataset` is the name of the dataset to search: (`organizations`, `users` or `tickets`)
- `index` is the name of the index for the dataset (too many to list, explore via tab completion or help)
- `query` is the text to search for.


Design
======

The fundamental interface of the system is `Store`, and its concrete implementation: `VectorStore`.
A store contains two things:

1) A way of getting at the underlying data, given a location. In the `VectorStore` case this is just a vector lookup, 
but the design supports extensions to more complex storage mechanisms, such as
files, REST calls, etc. These location => data lookups should be quick (constant or logarithmic).

2) A way of quickly (again, logarithmic or linear) looking up locations.

Then combining 1 and 2 gives us access to the data in two quick operations.

`VectorStore` uses a `Vector` for 1, (logarithmic operation) and a `HashMap` for 2.

Bulding a store
---------------
`VectorStore` takes a `Vector` of values, and one of `QueryGen`s (which is a way of generating index keys),
and traverses the Vector, storing each generated key in the hashmap.

`QueryGen`s come in two flavours: direct and join. Direct maps a value to a index key that is a function of
that value. Join is more interesting because it allows us to join datasets. It takes an store and a function
from value to an index key, thus allowing us to map values to keys from related datasets.

Once we have stores, then it is a little bit of manual work defining appropriate
stores for the data model provided. 


Tradeoffs/Limitations
=====================

### Static typing

I decided to statically type all of the data and all of the fields. This helps with maintainable
code, and helps us define new and correct indexes. Typed composition of functions with stores 
also gives us indexing by joinded datasets nearly for free. 

However this approach also means changes to the schema of the data (new fields, new datasets, etc.)
require code changes. A dynamically typed approach would be able to be schema agnostic and automatically map all json fields to their
string representation.

However joins and non .toString indexes would still require code, so in my opinion the static typing is worth it.

### Multiple traversals for index building

Presently each of the stores defined in the `Stores` object in `App.scala` traverses the dataset to build
the index. This makes for nice readable code, but for large datasets isn't the most efficient. We should 
build the indices for a dataset all together via one traversal per dataset.

### Not all of the columns are displayed per each dataset

This was a usability issue. I wanted something nicely presentable and readable. Because I am displaying it
in a table, I wanted to fit the table to the width of a typical fullscreen terminal (200 characters seemed 
reasonable, at least on my Macbook Pro). It is easy to add more columns in `Tables.scala`.

A more reasonable approach might be to extend the query language to allow the user to select fields, rather than hardcoding an arbitrary set.