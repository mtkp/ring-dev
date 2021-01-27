mtkp/ring-dev
=============

Run a development ring server with a `deps.edn` project.

This tool is designed to mimic [`lein-ring`](https://github.com/weavejester/lein-ring)
plugin if you're using tools.deps.alpha instead of leiningen. `ring-dev`
provides a way to run a development ring jetty server, similar to
`lein ring server`. Currently there are no plans to implement packaging
commands that are found in lein-ring (e.g. `lein ring uberwar`).

`ring-dev` also includes a few development utilities on top of ring
jetty adapter, see [options](#options) below.

# Usage

Add an alias to `deps.edn` (using `server` as an example, if you choose a different
alias name, then the subsequent example commands should be changed accordingly)

```clj
{...
 :aliases
 {:server {:extra-deps {mtkp/ring-dev {:git/url "https://github.com/mtkp/ring-dev"
                                       :sha "...."}}
           :main-opts ["-m" "mtkp.ring-dev.main" "your.ns/your-handler"]}}}
```

Then start a server using the alias added to `deps.edn`

```sh
clj -M:server
```

## Options

```sh
clj -M:server --port 45678    # start the server at port 45678
clj -M:server --browser       # open the server root in the default system browser
clj -M:server --no-reload     # disable runtime namespace reloading
clj -M:server --ring-debug --ring-spec # troubleshoot bad handler behavior
```

All options can be added to either `:main-opts` or included at the command line.
Run with `--help` to see the full list of options and default values (`--help` will not
start the server)

```sh
clj -M:server --help
```

See `example/` for a working example.
