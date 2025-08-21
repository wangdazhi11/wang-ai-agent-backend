<template>
  <section class="section">
    <!-- 顶部学习主题横幅 -->
    <div class="landing-hero">
      <div class="bg"></div>
      <div class="fg">
        <div class="badge">牛不牛比</div>
        <h2 class="landing-title">你志哥开发的智能学习与工作效率助手</h2>
        <p class="landing-sub">干就完了</p>
        <div class="cta-group">
          <router-link to="/study" class="btn">立即体验学习规划</router-link>
          <router-link to="/manus" class="btn secondary">试试超级智能体</router-link>
          <a class="btn ghost" href="https://example.com" target="_blank">查看文档</a>
        </div>
        <div class="stats">
          <span class="pill">多端自适应</span>
          <span class="pill">流式响应</span>
          <span class="pill">轻量快速</span>
        </div>
      </div>
    </div>

    <!-- 亮点功能区块 -->
    <div class="features">
      <div class="feature">
        <div class="row-gap-8"><div class="avatar study">学</div><h4>个性化学习路径</h4></div>
        <p>基于目标与时间，为你生成阶段目标、每日清单、里程碑与复盘提醒。</p>
      </div>
      <div class="feature">
        <div class="row-gap-8"><div class="avatar manus">智</div><h4>多工具智能体</h4></div>
        <p>整合检索、总结、格式转换与自动化执行，减少重复劳动，专注关键工作。</p>
      </div>
      <div class="feature">
        <div class="row-gap-8"><div class="avatar ai">AI</div><h4>实时流式反馈</h4></div>
        <p>后端 SSE 推流，前端即时呈现，让等待不再漫长，边看边用。</p>
      </div>
    </div>

    <!-- 预览骨架（增强高阶感） -->
    <div class="preview mt-20">
      <div class="bar"><span class="dot"></span><span class="dot"></span><span class="dot"></span></div>
      <div class="body">
        <div class="skel" style="width:60%"></div>
        <div class="skel" style="width:86%"></div>
        <div class="skel" style="width:72%"></div>
        <div class="skel" style="width:52%"></div>
      </div>
    </div>

    <!-- 应用入口卡片 -->
    <div class="grid mt-20">
      <article class="card">
        <div class="row-gap-8">
          <div class="avatar study">学</div>
          <div>
            <h3 class="card-title">学习规划大师</h3>
            <p class="card-desc">系统化制定学习计划与每日任务，适配不同阶段目标。</p>
          </div>
        </div>
        <router-link to="/study" class="btn" style="margin-top:12px; display:inline-flex;">进入对话</router-link>
      </article>

      <article class="card">
        <div class="row-gap-8">
          <div class="avatar manus">智</div>
          <div>
            <h3 class="card-title">超级智能体</h3>
            <p class="card-desc">多工具协作，处理复杂任务：调研、整理、格式转换与自动化。</p>
          </div>
        </div>
        <router-link to="/manus" class="btn secondary" style="margin-top:12px; display:inline-flex;">进入对话</router-link>
      </article>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.home { text-align: center; margin-top: 100px; }
.nav { display: flex; justify-content: center; gap: 16px; margin-top: 24px; }
.btn { padding: 10px 16px; border: 1px solid #409eff; color: #409eff; border-radius: 6px; text-decoration: none; }
.btn:hover { background: #ecf5ff; }
</style>