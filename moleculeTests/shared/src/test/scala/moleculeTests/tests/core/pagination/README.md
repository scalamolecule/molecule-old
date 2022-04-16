


Normal restrictions of offset pagination and SQL databases:
- gets slower and slower for large data sets



Fundamental difference:
- SQL returns possibly redundant rows (List semantics)
- Datomic returns unique rows (Set semantics)

This allows us to compare rows meaningfully



Normal cursor restrictions:
- Requires sorting on column(s) with unique values. Timestamps are only near-unique since multiple rows could have been transacted on the same millisecond (the minimum time unit of java.util-Date), so to be sorted correctly even timestamps would need additional data to ensure uniqueness and correct paging.
- An unchanged index on the sorted attributes should exist (I'm not sure if updates inbetween page loads are updating such an index in other systems, allowing changes to propagate to subsequent pages)
- A cursor row can't be changed or deleted.
- Has to be on "top level" to be unique in the output. Wouldn't be unique in output if it was in a joined table




- No rows should be duplicated or skipped on subsequent pages
- No intrusion on domain model, no creation/updates of sorting indexes
- Pagination should be generic to allow any molecule query sorted in any way on any attributes (!)
- Pagination strategies should be transparently resolved
- Paging should be high performant for large data sets




Drawbacks
- Change data inbetween page loads are not shown.

- Works on a snapshot of the database as of the moment the cursor is opened. So changes between page retrievals are not shown.

If new data is vital to be shown during paging, two options exist:
- call `getSince(t)` with the same query before each page load and let the user know that "new data exists" if data has been added after `t` (the basisT of the database state of the initial page).
- code a (likely more costly) cursor implementation where since-data is compared to/retracted from/merged with






---------------------
https://www.cockroachlabs.com/docs/stable/pagination.html

"operate on a snapshot of the database at the moment the cursor is opened"


----------------
https://www.django-rest-framework.org/api-guide/pagination/
Cursor based pagination requires that there is a unique, unchanging ordering of items in the result set. This ordering might typically be a creation timestamp on the records, as this presents a consistent ordering to paginate against.

Proper usage of cursor pagination should have an ordering field that satisfies the following:

Should be an unchanging value, such as a timestamp, slug, or other field that is only set once, on creation.
Should be unique, or nearly unique. Millisecond precision timestamps are a good example. This implementation of cursor pagination uses a smart "position plus offset" style that allows it to properly support not-strictly-unique values as the ordering.
Should be a non-nullable value that can be coerced to a string.
Should not be a float. Precision errors easily lead to incorrect results. Hint: use decimals instead. (If you already have a float field and must paginate on that, an example CursorPagination subclass that uses decimals to limit precision is available here.)
The field should have a database index.
