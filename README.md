![Clojure CI - Unit and Integration Tests](https://github.com/macielti/todo-list-api-clojure/actions/workflows/github_actions.yml/badge.svg)

# Microservice user management

I am tired of having to rewrite the user management component for every CRUD based service 😠!

So I made this microservice to reuse in future projects.

## Usage

It's not read for producition yet, but you can run a local version:

- Clone this repository and in your terminal run (with mocked kafka producer): `lein with-profile test run`
- If you want to check unit and integrations test run: `lein with-profile test test`
- If you want to see the coverage test report run (with mocked kafka producer): `lein with-profile test cloverage`

## License

Copyright © 2021 Bruno do Nascimento Maciel

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which
is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary Licenses when the conditions for such
availability set forth in the Eclipse Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your option) any later version, with the GNU
Classpath Exception which is available at https://www.gnu.org/software/classpath/license.html.
