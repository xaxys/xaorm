# xaorm

## ORM使用指北

0. orm的存在本身是为简化数据库操作，jdbc实在是又老掉牙又难用。本orm是纯手撸orm，结构简单，不像mybatis或hibernate那样复杂，半自动，有很多缺陷，但是可以满足大部分的需求。
1. 创建一个Engine类，使用sync方法，该方法接收一个或多个类的Class对象，用于自动创建/同步数据表。
2. 使用Engine.createSession方法创建一个Session对象，该对象可以用于执行数据库操作，不同SQL语句建议使用不同的Session对象。
3. 使用Session对象的schema方法设置Session对象的Schema，该方法接收一个类的Class对象，用于设置Session对象的Schema。
或使用Session对象的model方法设置Session对象的Schema，该方法接收一个对象，可视为schema的语法糖。
4. （可选）使用Session.where/Session.orderBy/Session.limit/Session.join等方法设置查询条件，相当于写SQL子句。
5. 使用Session.findOne/Session.findAll方法执行查询，该方法为终结操作，调用后无法继续进行链式调用。
6. 使用Session.insert/Session.update/Session.delete方法执行增删改操作，该方法为终结操作，调用后无法继续进行链式调用。
  - insert方法接收一个对象，插入一行。
  - update方法接收一个Map<String, Object>，用于指定更新的列和内容。
或直接接收String, Object对象，用于快速更新单一列和内容，可视为前者的语法糖。
  - delete方法无参数，请务必使用where方法设置筛选条件，否则将删除所有行！
7. 命名规范：
  - 实体一般采取PascalCase，ORM转换表名时会转换为snake_case。
  - 属性一般采取camelCase，ORM转换列名时会转换为snake_case。
8. 字段类型映射：
  - 基本类型不做赘述：int, long, float, double, boolean...
  - String类型：方便起见一律采用TEXT类型。其他类型如VARCHAR等，可根据需要自行添加@Column(type="VARCHAR(255)")。
  - 日期类型：虽然Java有多种日期类型，如Date, Time, Calendar等等。为方便映射，建议日期一律使用java.time.LocalDateTime，在数据库中对应DATETIME类型。

## Example

```java
Star s = Main.orm.createSession()
        .schema(Star.class)
        .where("user=?",user)
        .where("music=?",music)
        .where("deleted_at IS NULL")
        .findOne();

List<Star> stars = Main.orm.createSession()
        .schema(Star.class)
        .where("user=?", user)
        .where("deleted_at IS NULL")
        .findAll();

Main.orm.createSession()
        .schema(Star.class)
        .where("user=?", user)
        .where("music=?", music)
        .update("deleted_at", LocalDateTime.now());

Star s = new Star();
s.setUser(user);
s.setMusic(music);
s.setCreatedAt(LocalDateTime.now());
s.setDeletedAt(null);

Main.orm.createSession().model(s).where("user=?", user).where("music=?", music).delete();
Main.orm.createSession().model(s).insert(s);
```
