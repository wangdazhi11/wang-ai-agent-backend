<template>
  <div>
    <header class="site-header">
      <nav class="nav">
        <div class="container nav-inner">
          <div class="brand"><span class="brand-badge">AI</span> 得志的AI工具</div>
          <div class="nav-links">
            <router-link to="/">首页</router-link>
            <router-link to="/study">学习规划</router-link>
            <router-link to="/manus">超级智能体</router-link>
          </div>
        </div>
      </nav>
    </header>
    <main class="container section">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>