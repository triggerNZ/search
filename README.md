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
```

So three users were found, and some of their information was displayed.

Another example, let's find tickets submitted by Nash Rivers, but because we can,
search by his alias Mr Alexandria instead:

```
> tickets.submitter:Mr Alexandria
```

Query Syntax
------------

Queries are in the form `<dataset>.<index>:<query>`, where

- `dataset` is the name of the dataset to search: (`organizations`, `users` or `tickets`)
- `index` is the name of the index for the dataset (too many to list, explore via tab completion or help)
- `query` is the text to search for.


Help and tab completion
-----------------------

To get a view of all of the available indices, use the `help` command. There is also
tab completion. So you can start typing the name of a dataset or index and they will be completed for you.

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


Libraries
---------

- Circe  - for JSON processing
- Cats   - for some FP support
- Jline  - for the REPL and tab completion
- Univeq - for proofs of natural hashCode and equality so that HashSets can be used safely
- Utest  - for testing

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

### Manual decoders/indices

The JSON decoders for each type were defined manually in `Decoders.scala`. That has the disadvantage of boilerplate but the advantage
of more fine-grained control over field names (e.g. `domain_names` doesn't conform to usual scala naming 
conventions) and also over schema evolution over time.

Similarly, each index is automatically written. Again, it has the disadvantage of boilerplate but it also allows custom
indexes in the cases where they are required. A great example is `users.byNameOrAlias` which searches by either name or
alias.

### In-memory data

This approach could just as well extend to building indices from large random access files without taking all the data 
into memory. As long as the indices fit into memory, we could access much larger volumes of data. However, this wasn't 
implemented due to limited time in implementing this project.

### Test strategy

Utest was used as the testing library. Most of the tests rely on the example data to verify real-life queries. A notable test
is `SearchByEveryFieldTest` which verifies that every field in each json example can be found in one of the indices. 
This flushed out a lot of bugs.

To run the tests:

```
sbt test
```